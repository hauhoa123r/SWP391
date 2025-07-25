FROM ubuntu:latest
LABEL authors="ngoct"

ENTRYPOINT ["top", "-b"]