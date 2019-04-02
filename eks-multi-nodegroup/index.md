# Amazon EKS Cluster with Multiple Node Groups

This document explains how to create an Amazon EKS cluster with two node groups. The first node group has upto 2 GPUs and the second node group has up to 4 CPUs. This is useful if you want to run ML and non-ML workloads on the same cluster. Nodes are labeled with `role: gpu` and `role: cpu`.

## Create EKS cluster

- Subscribe to the GPU supported AMI:

	https://aws.amazon.com/marketplace/pp/B07GRHFXGM

- Install eksctl:

  ```
  brew tap weaveworks/tap
  brew install weaveworks/tap/eksctl
  ```

  OR

  ```
  brew upgrade eksctl
  ```

- Verify eksctl version:

  ```
  eksctl version
  [ℹ]  version.Info{BuiltAt:"", GitCommit:"", GitTag:"0.1.26"}
  ```

- Create a EKS cluster with two node groups:

	```
	eksctl create cluster -f eksctl-config.yaml
	[ℹ]  using region us-west-2
	[ℹ]  setting availability zones to [us-west-2c us-west-2d us-west-2b]
	[ℹ]  subnets for us-west-2c - public:192.168.0.0/19 private:192.168.96.0/19
	[ℹ]  subnets for us-west-2d - public:192.168.32.0/19 private:192.168.128.0/19
	[ℹ]  subnets for us-west-2b - public:192.168.64.0/19 private:192.168.160.0/19
	[ℹ]  nodegroup "ng-gpu" will use "ami-08377056d89909b2a" [AmazonLinux2/1.11]
	[ℹ]  nodegroup "ng-cpu" will use "ami-0ed0fe5ff74520950" [AmazonLinux2/1.11]
	[ℹ]  creating EKS cluster "gpu-cpu-cluster" in "us-west-2" region
	[ℹ]  will create a CloudFormation stack for cluster itself and 2 nodegroup stack(s)
	[ℹ]  if you encounter any issues, check CloudFormation console or try 'eksctl utils describe-stacks --region=us-west-2 --name=gpu-cpu-cluster'
	[ℹ]  building cluster stack "eksctl-gpu-cpu-cluster-cluster"
	[ℹ]  creating nodegroup stack "eksctl-gpu-cpu-cluster-nodegroup-ng-cpu"
	[ℹ]  creating nodegroup stack "eksctl-gpu-cpu-cluster-nodegroup-ng-gpu"
	[ℹ]  --nodes-min=2 was set automatically for nodegroup ng-gpu
	[ℹ]  --nodes-max=2 was set automatically for nodegroup ng-gpu
	[ℹ]  --nodes-min=4 was set automatically for nodegroup ng-cpu
	[ℹ]  --nodes-max=4 was set automatically for nodegroup ng-cpu
	[✔]  all EKS cluster resource for "gpu-cpu-cluster" had been created
	[✔]  saved kubeconfig as "/Users/argu/.kube/config"
	[ℹ]  adding role "arn:aws:iam::091144949931:role/eksctl-gpu-cpu-cluster-nodegroup-NodeInstanceRole-1TNZWK0D87YDU" to auth ConfigMap
	[ℹ]  nodegroup "ng-gpu" has 0 node(s)
	[ℹ]  waiting for at least 2 node(s) to become ready in "ng-gpu"
	[ℹ]  nodegroup "ng-gpu" has 2 node(s)
	[ℹ]  node "ip-192-168-11-163.us-west-2.compute.internal" is ready
	[ℹ]  node "ip-192-168-81-153.us-west-2.compute.internal" is ready
	[ℹ]  as you are using a GPU optimized instance type you will need to install NVIDIA Kubernetes device plugin.
	[ℹ]  	 see the following page for instructions: https://github.com/NVIDIA/k8s-device-plugin
	[ℹ]  adding role "arn:aws:iam::091144949931:role/eksctl-gpu-cpu-cluster-nodegroup-NodeInstanceRole-TQUU9HE286JB" to auth ConfigMap
	[ℹ]  nodegroup "ng-cpu" has 0 node(s)
	[ℹ]  waiting for at least 4 node(s) to become ready in "ng-cpu"
	[ℹ]  nodegroup "ng-cpu" has 4 node(s)
	[ℹ]  node "ip-192-168-15-38.us-west-2.compute.internal" is ready
	[ℹ]  node "ip-192-168-16-204.us-west-2.compute.internal" is ready
	[ℹ]  node "ip-192-168-59-95.us-west-2.compute.internal" is ready
	[ℹ]  node "ip-192-168-84-10.us-west-2.compute.internal" is ready
	[ℹ]  kubectl command should work with "/Users/argu/.kube/config", try 'kubectl get nodes'
	[✔]  EKS cluster "gpu-cpu-cluster" in "us-west-2" region is ready
	```

