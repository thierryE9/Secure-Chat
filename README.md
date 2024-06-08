# Secure Chat

## Overview

This project is a Java-based secure chatting application featuring end-to-end encryption and user authentication. The application employs a client-server architecture where all requests are sent as JSON objects. It ensures that all messages are securely transmitted and stored if the recipient is offline.

## Features

- **End-to-End Encryption**: All communications between clients are encrypted to ensure privacy.
- **User Authentication**: Users must sign in to access the chat application.
- **JSON Communication**: Requests and responses are formatted as JSON objects for easy parsing.
- **Message Forwarding**: The server forwards messages to the intended recipient.
- **Offline Message Storage**: Messages are stored on the server if the recipient is offline and delivered when they come online.

## How It Works

1. **Start the Server**: Begin by running the server application.
2. **Set Server Address**: Configure the client's server address variable to match the server's IPv4 address.
3. **Login**: Users must log in to access the chat functionality.
4. **Chat**: Users can send and receive messages in real-time.
5. **Sign Out**: Users can log out when they are done.

## Getting Started

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/thierryE9/Secure-Chat.git
   ```

2. **Build the Project**
   - Use your preferred IDE to open the project and build the server and the client.

### Running the Application

1. Start the server
2. Start the clients

### Usage

1. **Login**: Enter your credentials to log in.
2. **Chat**: Start sending messages to other users.
3. **Sign Out**: Log out when you are done chatting.
