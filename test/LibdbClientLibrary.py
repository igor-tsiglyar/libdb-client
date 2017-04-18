import requests
from robot.api import logger
from robot.libraries.BuiltIn import BuiltIn
import json


class LibdbClientLibrary(object):

    def __init__(self):
        self._url = self._get_global("server_url")
        self._session = requests.Session()
        self._result = self._post_request(self._url + '/login',
                                          data={'email': self._get_global("login"),
                                                'password': self._get_global("password")})
        logger.console(self._result)

    def _get_global(self, name):
        return BuiltIn().get_variable_value('${%s}' % name) 

    def _get_request(self, url, data=None):
        try:
            return self._sesssion.get(url, json=data, timeout=10).text
        except requests.exceptions.RequestException:
            return ''
        
    def _post_request(self, url, data=None):
        try:
            logger.console(data)
            return self._session.post(url, json=data, timeout=10).text
        except requests.exceptions.RequestException:
            return ''

    def login(self):
        pass

    def add_client(self, json_str):
        self._result = self._post_request(self._url + '/clients/add', json.loads(json_str))

    def result_should_contain(self, expected):
        for keyword in expected.split('+'):
            if keyword not in self._result:
                raise AssertionError('%s != %s' % (self._result, expected))
