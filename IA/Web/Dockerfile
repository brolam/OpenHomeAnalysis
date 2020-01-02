FROM python:3.7
ENV PYTHONUNBUFFERD = 1
ENV LISTEN_PORT=8000
EXPOSE 8000
COPY ./requirements.txt requirements.txt
RUN pip install --upgrade pip
RUN pip install -r /requirements.txt
RUN curl -sL https://deb.nodesource.com/setup_13.x | bash -
RUN apt-get install -y nodejs
WORKDIR /IA