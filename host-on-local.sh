VM_IP_ADDRESS=192.168.122.35
VM_IP_RANGE=192.168.122.0/24
VM_PORT=80 # service running on this port in vm
VIRTUAL_BRIDGE=virbr0 # virtual bridge that manages virtual network
HOST_PORT=80 # LAN devices will use this port when connecting to host
HOST_INTERFACE=enp4s0 # eth0 or wlan0
# connections from outside
sudo iptables -I FORWARD -o $VIRTUAL_BRIDGE -d  $VM_IP_ADDRESS -j ACCEPT
sudo iptables -t nat -I PREROUTING -p tcp --dport $HOST_PORT -j DNAT --to $VM_IP_ADDRESS:$VM_PORT

# Masquerade local subnet
sudo iptables -I FORWARD -o $VIRTUAL_BRIDGE -d  $VM_IP_ADDRESS -j ACCEPT
sudo iptables -t nat -A POSTROUTING -s $VM_IP_RANGE -j MASQUERADE
sudo iptables -A FORWARD -o $VIRTUAL_BRIDGE -m state --state RELATED,ESTABLISHED -j ACCEPT
sudo iptables -A FORWARD -i $VIRTUAL_BRIDGE -o $HOST_INTERFACE -j ACCEPT
sudo iptables -A FORWARD -i $VIRTUAL_BRIDGE -o lo -j ACCEPT
