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

Add book
    Add book                 {"title": "Test Book", "amount": 5, "typeId": 1}
    Result should contain    Test Book + 5 books avaliable

Borrow book
    Borrow book              {"firstName": "John", "lastName": "Doe", "passportSer": "0123", "passportNum": "456789", "title": "Test Book"}
    Result should contain    Test Book

Search book after borrow
    Search book              Test Book
    Result should contain    Test Book + 4 books avaliable

Return book
    Return book                  {"firstName": "John", "lastName": "Doe", "passportSer": "0123", "passportNum": "456789", "title": "Test Book"}
    Result should not contain    Test Book

Search book after return
    Search book              Test Book
    Result should contain    Test Book + 5 books avaliable

Delete client
    Delete client            {"firstName": "John", "lastName": "Doe", "passportSer": "0123", "passportNum": "456789"}
    Result should contain    No such client

Delete book
    Delete books             {"title": "Test Book", "amount": 5}
    Result should contain    No such book
