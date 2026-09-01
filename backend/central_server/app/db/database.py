from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker


# Yaha SQLite ka local DB connection define ho raha hai.
DATABASE_URL = "sqlite:///./central_server.db"

engine = create_engine(
	DATABASE_URL,
	connect_args={"check_same_thread": False},
)

# SessionLocal se har request ke liye DB session milega.
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


# Dependency function jo route me DB session inject karega.
def get_db():
	db = SessionLocal()
	try:
		yield db
	finally:
		db.close()
