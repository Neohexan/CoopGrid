from datetime import datetime, timedelta, timezone
from typing import Optional

from sqlalchemy import Boolean, Column, DateTime, Integer, String
from sqlalchemy.orm import Session

from app.db.database import Base, engine
import logging
import sqlalchemy

logger = logging.getLogger("app.otp.otp")


# Ye table OTP lifecycle track karega: create, verify aur success status.
class OtpRecord(Base):
	__tablename__ = "otp_records"

	id = Column(Integer, primary_key=True, index=True)
	phone_number = Column(String(20), nullable=False, index=True)
	device_id = Column(String(255), nullable=True, index=True)
	user_id = Column(String(128), nullable=True, index=True)
	otp = Column(String(6), nullable=False, index=True)
	created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
	verified_at = Column(DateTime, nullable=True)
	is_verified_success = Column(Boolean, default=False, nullable=False)


def _build_time_based_otp(reference_time: datetime) -> str:
	# OTP format DDHHMM hoga, example: 05-13-17 => 051317
	return f"{reference_time.day:02d}{reference_time.hour:02d}{reference_time.minute:02d}"



def generate_otp(db: Session, phone_number: str, device_id: Optional[str] = None, user_id: Optional[str] = None) -> OtpRecord:
	# OTP generation will use India Standard Time (UTC+05:30) for the time-based value
	# but the record's `created_at` is stored in UTC (naive) so verification remains consistent.
	utc_now = datetime.now(timezone.utc)
	india_tz = timezone(timedelta(hours=5, minutes=30))
	india_now = utc_now.astimezone(india_tz)
	generated_otp = _build_time_based_otp(india_now)

	# Inspect DB table columns to decide whether to include device_id in insert
	include_device = False
	try:
		inspector = sqlalchemy.inspect(engine)
		columns = [c['name'] for c in inspector.get_columns('otp_records')]
		include_device = 'device_id' in columns
	except Exception:
		# If inspection fails, be conservative and omit device_id to avoid insert errors
		include_device = False

	if include_device:
		record = OtpRecord(
			phone_number=phone_number,
			device_id=device_id,
			user_id=str(user_id) if user_id else None,
			otp=generated_otp,
			created_at=utc_now.replace(tzinfo=None),
		)
	else:
		record = OtpRecord(
			phone_number=phone_number,
			user_id=str(user_id) if user_id else None,
			otp=generated_otp,
			created_at=utc_now.replace(tzinfo=None),
		)

	try:
		db.add(record)
		db.commit()
		db.refresh(record)
		return record
	except Exception as exc:
		logger.exception("failed to insert otp record: %s", exc)
		try:
			db.rollback()
		except Exception:
			pass
		raise



def verify_otp(db: Session, phone_number: str, device_id: Optional[str], otp_input: str) -> dict:
	# Latest OTP record ko check karke 2 min validity + +2 tolerance apply karenge.
	query = db.query(OtpRecord).filter(OtpRecord.phone_number == phone_number)
	if device_id:
		query = query.filter(OtpRecord.device_id == device_id)
	record = query.order_by(OtpRecord.created_at.desc()).first()

	if not record:
		return {"verified": False, "reason": "OTP record not found"}

	now = datetime.utcnow()
	if now > (record.created_at + timedelta(minutes=2)):
		record.verified_at = now
		record.is_verified_success = False
		db.commit()
		return {"verified": False, "reason": "OTP expired"}

	# User requirement: original OTP se +2 value tak valid maana jayega.
	allowed_otps = {
		record.otp,
		f"{(int(record.otp) + 1) % 1000000:06d}",
		f"{(int(record.otp) + 2) % 1000000:06d}",
	}

	is_valid = otp_input in allowed_otps
	record.verified_at = now
	record.is_verified_success = is_valid
	db.commit()

	return {
		"verified": is_valid,
		"reason": "OTP verified successfully" if is_valid else "Invalid OTP",
		"created_at": record.created_at,
		"verified_at": record.verified_at,
	}
