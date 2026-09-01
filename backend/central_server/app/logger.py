import logging
import sys

# Configure Logger Format
LOG_FORMAT = "%(asctime)s | %(levelname)-8s | %(name)s:%(funcName)s:%(lineno)d - %(message)s"

def setup_logger():
    logger = logging.getLogger("central_server")
    logger.setLevel(logging.INFO)

    # Avoid duplicate logs if handlers already exist
    if not logger.handlers:
        # 1. Console Handler (Terminal Output)
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setFormatter(logging.Formatter(LOG_FORMAT))
        logger.addHandler(console_handler)

        # 2. File Handler (Saves logs to central_server.log file)
        file_handler = logging.FileHandler("central_server.log", encoding="utf-8")
        file_handler.setFormatter(logging.Formatter(LOG_FORMAT))
        logger.addHandler(file_handler)

    return logger

logger = setup_logger()