import json
import requests
from robot.api import logger
from robot.libraries.BuiltIn import BuiltIn
from lxml import html


class LibdbClientLibrary(object):

    def __init__(self):
        self._base_url = self._get_global('server_url')
        self._session = requests.Session()
        self._login()


    def _get_global(self, name):
        return BuiltIn().get_variable_value('${%s}' % name)


    def _get_request(self, url, data=None):
        try:
            return self._session.get(url, json=data).content
        except requests.exceptions.RequestException as e:
            logger.console(e)
            return ''

    
    def _post_request(self, url, data=None):
        try:
            return self._session.post(url, json=data).content
        except requests.exceptions.RequestException as e:
            logger.console(e)
            return ''


    def _login(self):
        login = self._get_global('login')
        password = self._get_global('password')
        self._result = self._post_request(self._base_url + '/login',
                                          {'email': login, 'password': password})


    def login(self):
        pass


    def logout(self):
        self._result = self._get_request(self._base_url + '/logout')


    def _get_input_default_value(self, page, id):
        tree = html.fromstring(page)
        value = tree.xpath('//input[@id="%s"]/@value' % id)

        if len(value) > 0:
            return value[0]

        return None


    def _search_client(self, json_str):
        return self._post_request(self._base_url + '/clients/search',
                                  json.loads(json_str))


    def search_client(self, json_str):
        self._result = self._search_client(json_str)


    def add_client(self, json_str):
        self._result = self._post_request(self._base_url + '/clients/add',
                                          json.loads(json_str))
 
    def delete_client(self, json_str):
        client_id = self._get_input_default_value(self._search_client(json_str), 'id')

        if client_id is None:
            self._result = ''
        else:
            self._post_request(self._base_url + '/clients/delete',  
                               {'id': client_id})
            self._result = self._search_client(json_str)


    def _search_book(self, title):
        return self._post_request(self._base_url + '/books/search',
                                  {'title': title})


    def search_book(self, title):
        self._result = self._search_book(title)


    def add_book(self, json_str):
        self._result = self._post_request(self._base_url + '/books/add',
                                          json.loads(json_str))


    def delete_books(self, json_str):
        kwargs = json.loads(json_str)
        book_id = self._get_input_default_value(self._search_book(kwargs['title']), 'id')

        if book_id is None:
            self._result = ''
        else:
            self._post_request(self._base_url + '/books/delete',
                               {'id': book_id, 'amount': kwargs['amount']})
            self._result = self._search_book(kwargs['title'])

    def borrow_book(self, json_str):
        kwargs = json.loads(json_str)
        book_title = kwargs.pop('title')
        json_str = json.dumps(kwargs)

        book_id = self._get_input_default_value(self._search_book(book_title), 'id')
        client_id = self._get_input_default_value(self._search_client(json_str), 'id')

        self._post_request(self._base_url + '/journal/add',
                           {'bookId': book_id, 'clientId': client_id})

        self._result = self._get_request(self._base_url + '/books/client/' + client_id)


    def return_book(self, json_str):
        kwargs = json.loads(json_str)
        book_title = kwargs.pop('title')
        json_str = json.dumps(kwargs)

        book_id = self._get_input_default_value(self._search_book(book_title), 'id')
        client_id = self._get_input_default_value(self._search_client(json_str), 'id')

        self._post_request(self._base_url + '/journal/delete',
                           {'bookId': book_id, 'clientId': client_id})

        self._result = self._get_request(self._base_url + '/books/client/' + client_id)


    def result_should_contain(self, expected):
        for keyword in expected.split(' + '):
            if keyword not in self._result:
                raise AssertionError('%s != %s' % (self._result, expected))


    def result_should_not_contain(self, expected):
        for keyword in expected.split(' + '):
            if keyword in self._result:
                raise AssertionError('%s != %s' % (self._result, expected))
