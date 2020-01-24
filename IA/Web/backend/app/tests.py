from django.urls import reverse
from django.contrib.auth.models import User
from django.test import TestCase
from rest_framework.test import APIClient

class BDD_Tests(TestCase):
    def setUp(self):
        # Every test needs access to the request factory.
        self.client = APIClient()
        self.username='tester' 
        self.password='top_secret'
        self.user = User.objects.create_user(username=self.username, email='tester@brolam.com.br', password=self.password)

    def test_was_imported_energy_log_batch(self):
        url = reverse('token-auth')
        data = {'username': self.username, 'password':self.password}
        response = self.client.post(url, data , format='json')
        print(response.data)
        self.assertEqual(response.status_code, 200)