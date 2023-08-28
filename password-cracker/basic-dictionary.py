import requests
import json

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

password_list = ["list", "of", "potential", "passwords", "abc"]

for password in password_list:
    print(f"Testing: {password}")
    if try_password(password):
        print(f"Found correct password: {password}")
        break

