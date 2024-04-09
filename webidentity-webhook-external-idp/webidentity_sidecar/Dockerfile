FROM alpine
COPY get_token.sh get_token.sh
RUN chmod +x get_token.sh
#CMD ["./get_token.sh"]
CMD ["sh", "-c", "while true; do ./get_token.sh; sleep 3000; done"]