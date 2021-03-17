from socket import *

HOST = ''
PORT = 8888
ADDR = (HOST, PORT)

tcpSerSock = socket(AF_INET, SOCK_STREAM)
tcpSerSock.bind(ADDR)
tcpSerSock.listen(5)

print ('Waiting for connection...')


while True:
    (conn,addr) = tcpSerSock.accept()
    
    while True:
        data = conn.recv(1024).decode('utf-8')
        
        if not data:
            break
        
        data = data[:-1]
        
        if data == "hello" or data == "hi" or data == "Hello" or data =="Hi":
            reply = "Hello! This is Raspberry Pi\n"
        elif data == "start" or data == "START":
            reply = "Car Started\n"
        elif data == "stop" or data == "STOP" :
            reply = "Car Stopped\n"
        elif data == "lights off" or data == "turn off lights":
            reply = "Lights turned Off\n"
        else:
            reply = "Type Specific Commands\n"
        
        print (data)
        conn.send(reply.encode('utf-8'))
    
conn.close()
