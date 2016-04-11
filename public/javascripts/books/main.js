
$.getJSON("/books.json", function(json) {
    json.splice(0, 0, {"title": "TITLE", "amount": "AMOUNT", "typeID": "TYPE ID"});
    $.jsontotable(json, {id: "#jsontotable"});
});