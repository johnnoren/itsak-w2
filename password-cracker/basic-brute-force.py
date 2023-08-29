import requests
import json
import itertools
import time

def try_password(password):
    headers = {'Content-Type': 'application/json'}
    payload = {
        "username": "username",
        "password": password
    }
    
    response = requests.post("http://localhost:8080/login", data=json.dumps(payload), headers=headers)
    
    if response.status_code == 200:
        return True
    return False

# ASCII characters from 33 to 126
chars = [chr(i) for i in range(33, 127)]

start_time = time.time()
password_found = False

# Start from length 1 and increase
for length in range(1, 6):  # Adjust the upper limit as required
    if password_found:
        break
    for combo in itertools.product(chars, repeat=length):
        password = ''.join(combo)
        print(f"Testing: {password}")
        if try_password(password):
            elapsed_time = time.time() - start_time
            print(f"Found correct password: {password}")
            print(f"Time taken to find the password: {elapsed_time:.2f} seconds")
            password_found = True
            break
