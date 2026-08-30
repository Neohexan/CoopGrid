from fastapi import APIRouter, HTTPException, status, Depends, Request
from pydantic import BaseModel
import logging
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.otp.otp import generate_otp
from app.otp.otp import verify_otp, OtpRecord
from datetime import datetime
from typing import List
import uuid


# Ye router auth service ke endpoints handle karega.


