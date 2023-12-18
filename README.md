[![CD](https://github.com/derBobby/p2signal-notification/actions/workflows/test-and-deploy.yml/badge.svg)](https://github.com/derBobby/p2signal-notification/actions/workflows/test-and-deploy.yml) [![Merge Dependabot PR](https://github.com/derBobby/p2signal-notification/actions/workflows/dependabot-automerge.yml/badge.svg)](https://github.com/derBobby/p2signal-notification/actions/workflows/dependabot-automerge.yml)

# p2signal-notification
This Spring Boot application sends Signal messages for incoming Pretix Webhooks.
It uses the [java-signal-connector](https://github.com/derBobby/java-signal-connector) which in turn needs a running docker container of [signal-cli-rest-api](https://github.com/bbernhard/signal-cli-rest-api)

## Features
* Listen for Webhooks and send notification to given recipients list
