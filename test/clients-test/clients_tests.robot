*** Settings ***
Library    ../LibdbClientLibrary.py

*** Variables ***
${server_url}    http://192.168.1.149:9000
${login}         admin@email.com
${password}      oracle

*** Test Cases ***
Add client
    Add client               {"firstName": "John", "lastName": "Doe", "passportSer": "0123", "passportNum": "456789"}
    Result should contain    John Doe + Passport 0123 456789

Search client
    Search client            {"firstName": "John", "lastName": "Doe", "passportSer": "0123", "passportNum": "456789"}
    Result should contain    John Doe + Passport 0123 456789

Delete client
    Delete client            {"firstName": "John", "lastName": "Doe", "passportSer": "0123", "passportNum": "456789"}
    Result should contain    No such client
