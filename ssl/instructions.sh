# ca.key: Certificate Authority private key file (this should not be shared)
# ca.crt: Certificate Authority trust certificate (this should be shared with users)
# server.key: Server Private key - password protected
# server.csr: Server certificate signing request (this should be shared only with the CA owner)
# server.crt: Server certificate signed by the CA - keep on server (this would be sent back the CA owner)
# server.pem: Conversion of server.key into a format gRPC likes (this shouldn't be shared)

# step 1: generate CA + Trust certificate (ca.crt + ca.key)
openssl genrsa -passout pass:1111 -des3 -out ca.key 4096
openssl req -passin pass:1111 -new -x509 -days 365 -key ca.key -out ca.crt -subj "/CN=localhost"

# step 2: generate the server private key (server.key)
openssl genrsa -passout pass:1111 -des3 -out server.key 4096

# step 3: get a certificate signing request from the CA (server.csr)
openssl req -passin pass:1111 -new -key server.key -out server.csr -subj "/CN=localhost"

# step 4: sign the certificate with the CA created in previous step (it's called self signing) - server.crt
openssl x509 -req -passin pass:1111 -days 365 -in server.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out server.crt

# step 5: convert the server certificate to .pem format (server.pem) - usable by gRPC
openssl pkcs8 -topk8 -nocrypt -passin pass:1111 -in server.key -out server.pem
