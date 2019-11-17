FROM tensorflow/tensorflow:latest-gpu-py3
ENV PYTHONUNBUFFERD = 1
ENV LISTEN_PORT=8000
EXPOSE 8000
COPY ./requirements.txt requirements.txt
RUN pip install --upgrade pip
RUN pip install -r /requirements.txt
RUN mkdir /app
WORKDIR /app
COPY ./app /app
