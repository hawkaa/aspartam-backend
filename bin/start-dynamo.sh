#!/usr/bin/env bash

virtualenv venv
source venv/bin/activate
pip install localstack==0.8.6
export SERVICES=dynamodb:4569
localstack start
