# 🌍 Offline-First, Incentivized Internet Sharing Community Network

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A community-driven project prototyped in **Mbare, Zimbabwe** during the Internet Society 2019 Chapterthon.  
The system enables **offline-first connectivity** and **incentivized internet sharing**, ensuring residents stay connected even when the broader internet is unavailable.

---

## ✨ Project Overview

This project combines:

- **Android Mobile Application** – an offline-first app for access to cached educational and community content (e.g., Kolibri).  
- **Community Network CDN** – a cluster of Raspberry Pis orchestrated with Kubernetes, serving as a local content distribution backbone.  
- **Blockchain Incentives** – built on the **Hype Open Protocol (HOP)** SDK, enabling tokenized peer-to-peer communication over Wi‑Fi Direct, Bluetooth, and local Wi‑Fi.

👉 The model empowers community groups (like **IAmMbare**) to **share excess fixed‑plan internet** with neighbors while earning **HOP tokens** for their contributions.

---

## 🛠️ Quick Start

### Mobile Application Setup
1. Clone this repository  
   ```bash
   git clone https://github.com/<your-org>/<repo>.git
   ```
2. Build the APK  
   ```bash
   ./gradlew assembleDebug
   ```
3. Install on a **physical Android device** (emulators are not supported).  

> **Note:** The app requires device capabilities (Bluetooth, Wi‑Fi Direct, etc.) that are not available on emulators.

---

### Kubernetes Master Setup
```bash
MASTER=$(hostname)

curl -sfL https://get.k3s.io -o install.sh
chmod +x install.sh
./install.sh server --kubelet-arg="address=0.0.0.0"

systemctl status k3s
sudo apt update && sudo apt install -y jq vim git

kubectl taint nodes $MASTER node-role.kubernetes.io/master=true:NoSchedule
kubectl label node $MASTER kubernetes.io/role=master node-role.kubernetes.io/master
```

---

### Kubernetes Node Setup
```bash
K3SMASTER="IAmMbare"
K3SMASTERIPADDRESS="192.168.1.7"    # Static IP Address
NODE_TOKEN="<replace-with-generated-token>"
NODE=$(hostname)

echo "$K3SMASTERIPADDRESS   $K3SMASTER" | sudo tee -a /etc/hosts

curl -sfL https://get.k3s.io -o install.sh
chmod +x install.sh
./install.sh agent --server https://$K3SMASTER:6443   --kubelet-arg="address=0.0.0.0"   --token $NODE_TOKEN

systemctl status k3s-agent
sudo apt update && sudo apt install -y jq vim

kubectl label node $NODE kubernetes.io/role=agent node-role.kubernetes.io/agent
```

---

## 📐 Architecture

```
[ Android App ]  <--->  [ HOP Protocol (P2P mesh) ]  
         |                          |  
   [ Local CDN: Raspberry Pi Cluster + k3s ]  
         |  
  [ Cached Content: Kolibri, Community Resources ]  
```

- **Offline-first**: Users access cached educational & community content.  
- **Mesh-first**: Devices communicate peer-to-peer via HOP even without internet.  
- **Incentivized**: Contributors earn tokens for sharing excess connectivity.  

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).  

---

## 🤝 Partners

- HypeLabs  
- Internet Society Zimbabwe Chapter  
- IAmMbare Youth Development Centre  
- St Peters IoT Makerspace  

---

## 🌱 Why This Matters
Access to the internet is increasingly essential, yet connectivity gaps remain.  
This project demonstrates how **decentralized, offline-first, and incentivized community networking** can bring knowledge, opportunity, and resilience to underserved communities.
