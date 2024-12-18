

FROM rust:latest
WORKDIR /server
COPY . .
RUN cargo build -r
CMD ["./target/release/server_rs"]
