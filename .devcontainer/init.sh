#!/bin/bash
set -e

# 기본 유틸 설치
sudo apt-get update
sudo apt-get install -y net-tools iputils-ping unzip curl python3-pip pipx
pipx ensurepath

# httpie 설치
pipx install httpie

# kubectl 설치
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
rm kubectl

# awscli 설치
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "../awscliv2.zip"
unzip -o ../awscliv2.zip
sudo ./aws/install 

# eksctl 설치
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
