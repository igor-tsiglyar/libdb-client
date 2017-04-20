*** Settings ***
Library    ../LibdbClientLibrary.py

*** Variables ***
${server_url}    http://192.168.1.149:9000
${login}         admin@email.com
${password}      oracle

*** Test Cases ***
Add book
    Add book                 {"title": "Test Book", "amount": 5, "typeId": 2}
    Result should contain    Test Book + 5 books avaliable

Search book
    Search book              Test Book
    Result should contain    Test Book + 5 books avaliable

Delete book
    Delete books             {"title": "Test Book", "amount": 5}
    Result should contain    No such book
