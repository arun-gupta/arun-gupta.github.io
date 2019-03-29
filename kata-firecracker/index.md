# Kata containers using Firecracker on Kubernetes

Kata Containers 1.5 added support for [Firecracker](https://aws.amazon.com/blogs/opensource/kata-containers-1-5-firecracker-support/). This document explains how to provision Kubernetes pods using Kata and Firecracker.

## Create EKS cluster

- Install eksctl:

  ```
  brew tap weaveworks/tap
  brew install weaveworks/tap/eksctl
  ```

  OR

  ```
  brew upgrade eksctl
  ```
- Create a K8s 1.12 cluster:

  ```
  eksctl create cluster --name myeks-1-12 --version 1.12
  ```