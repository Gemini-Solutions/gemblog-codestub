FROM ubuntu:latest

# Install bash and jq
RUN apt-get update && \
    apt-get install -y bash jq curl && \
    apt-get clean

COPY device_flow.sh device_flow.sh
RUN chmod 777 device_flow.sh
# Set the default shell to bash
ENTRYPOINT ["./device_flow.sh"]