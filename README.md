## Offline-First, Incentivized Internet Sharing Community Network

The project consists of an Android mobile application and Content Distribution Network (CDN) intended for offline-first, incentivized internet sharing, and protoyped within the Mbare community as part of the Internet Society 2019 Chapterthon Project. The goal of the system is to ensure residents will remain locally connected, initially and to have access to essential content made available through "offline-first" services like Kolibri (formerly Khan Academy Lite) and cached content. 

## Project  
 
This project utilises Blockchain-based Hype Open Protocol (HOP), which is an SDK for cross-platform peer-to-peer communication with mesh networking. HOP works even without Internet access, connecting devices via other communication channels such as Bluetooth, Wi-Fi direct, and Infrastructural Wi-Fi.    

The Community Network, developed using HOP, enables IAmMbare, a youth development non-governmental organisation, to share its excess fixed-plan Internet with locals whilst getting incentivized through payment of HOP tokens. iMbare’s offline-first CN is implemented as a Content Distribution Network, created from a cluster of Raspberry Pis orchestrated by Kubernetes.  
  
## Mobile Application Setup  
 
 The following are the necessary steps to configure it:  
  
 1. Download the source code
 2. Build the APK
 3. Run it on your android device!

  
Please note that the app can **ONLY be run on physical hardware devices**. Running on *emulators will not work* due to APIs related to certain features being unsupported.  

## Kubernetes Master Setup  

MASTER=$(hostname)

curl -sfL https://get.k3s.io -o install.sh
chmod +x install.sh
./install.sh server --kubelet-arg="address=0.0.0.0"
systemctl status k3s

sudo apt update
sudo apt install jq vim git -y
kubectl taint nodes $MASTER node-role.kubernetes.io/master=true:NoSchedule
kubectl label node $MASTER kubernetes.io/role=master node-role.kubernetes.io/master


## Kubernetes Nodes Setup
K3SMASTER="IAmMbare"
K3SMASTERIPADDRESS="192.168.1.7"    #Static IP Address
NODE_TOKEN=""       #Replace with generated token
NODE=$(hostname)

echo "$K3SMASTERIPADDRESS       $K3SMASTER" | sudo tee -a /etc/hosts
curl -sfL https://get.k3s.io -o install.sh
chmod +x install.sh
./install.sh agent --server https://$K3SMASTER:6443 --kubelet-arg="address=0.0.0.0" --token $NODE_TOKEN
systemctl status k3s-agent

sudo apt update
sudo apt install jq vim -y

kubectl label node $NODE kubernetes.io/role=agent node-role.kubernetes.io/agent


## License  
  
This project is MIT-licensed.  
    
## Partners
  
This project is in partnership with:
- HypeLabs
- Internet Society Zimbabwe Chapter
- IAmMbare Youth Development Centre
- St Peters IoT Makerspace
