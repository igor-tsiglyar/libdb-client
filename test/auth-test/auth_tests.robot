*** Settings ***
Library    ../LibdbClientLibrary.py

*** Variables ***
${server_url}    http://192.168.1.149:9000
${login}         admin@email.com
${password}      oracle

*** Test Cases ***
Login
    Login
    Result should contain    Logout

Logout
    Logout
    Result should contain    Login
