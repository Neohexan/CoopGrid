from datetime import datetime, timezone, timedelta

# Option A: Normal Naive Datetime (Local Time without timezone key)
def get_current_ist_datetime():
    # IST = UTC + 5:30
    ist_offset = timezone(timedelta(hours=5, minutes=30))
    # tzinfo=None karne se ZoneInfo Key error bilkul khatam ho jata hai
    return datetime.now(ist_offset).replace(tzinfo=None)

# Option B: Timestamp in Epoch Milliseconds (Integer)
def get_current_ist_epoch_ms() -> int:
    ist_offset = timezone(timedelta(hours=5, minutes=30))
    return int(datetime.now(ist_offset).timestamp() * 1000)