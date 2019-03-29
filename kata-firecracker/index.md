# Kata containers using Firecracker on Kubernetes

Kata Containers 1.5 added support for [Firecracker](https://aws.amazon.com/blogs/opensource/kata-containers-1-5-firecracker-support/). This document explains how to provision Kubernetes pods using Kata and Firecracker.

Detailed instructions for the Kata part are at https://github.com/kata-containers/packaging/tree/master/kata-deploy#kubernetes-quick-start.

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

- Verify eksctl version:

  ```
  eksctl version
  [ℹ]  version.Info{BuiltAt:"", GitCommit:"", GitTag:"0.1.26"}
  ```

- Create a K8s 1.12 cluster:

	```
	eksctl create cluster --name kata-eks --version 1.12
	[ℹ]  using region us-west-2
	[ℹ]  setting availability zones to [us-west-2c us-west-2b us-west-2a]
	[ℹ]  subnets for us-west-2c - public:192.168.0.0/19 private:192.168.96.0/19
	[ℹ]  subnets for us-west-2b - public:192.168.32.0/19 private:192.168.128.0/19
	[ℹ]  subnets for us-west-2a - public:192.168.64.0/19 private:192.168.160.0/19
	[ℹ]  nodegroup "ng-06b718b6" will use "ami-0280ac619ed294a8a" [AmazonLinux2/1.12]
	[ℹ]  creating EKS cluster "kata-eks" in "us-west-2" region
	[ℹ]  will create 2 separate CloudFormation stacks for cluster itself and the initial nodegroup
	[ℹ]  if you encounter any issues, check CloudFormation console or try 'eksctl utils describe-stacks --region=us-west-2 --name=kata-eks'
	[ℹ]  building cluster stack "eksctl-kata-eks-cluster"
	[ℹ]  creating nodegroup stack "eksctl-kata-eks-nodegroup-ng-06b718b6"
	[ℹ]  --nodes-min=2 was set automatically for nodegroup ng-06b718b6
	[ℹ]  --nodes-max=2 was set automatically for nodegroup ng-06b718b6
	[✔]  all EKS cluster resource for "kata-eks" had been created
	[✔]  saved kubeconfig as "/Users/argu/.kube/config"
	[ℹ]  adding role "arn:aws:iam::091144949931:role/eksctl-kata-eks-nodegroup-ng-06b7-NodeInstanceRole-EZCUDW5U2KKW" to auth ConfigMap
	[ℹ]  nodegroup "ng-06b718b6" has 0 node(s)
	[ℹ]  waiting for at least 2 node(s) to become ready in "ng-06b718b6"
	[ℹ]  nodegroup "ng-06b718b6" has 2 node(s)
	[ℹ]  node "ip-192-168-21-42.us-west-2.compute.internal" is ready
	[ℹ]  node "ip-192-168-90-113.us-west-2.compute.internal" is ready
	[ℹ]  kubectl command should work with "/Users/argu/.kube/config", try 'kubectl get nodes'
	[✔]  EKS cluster "kata-eks" in "us-west-2" region is ready
	```

- Install Kata on EKS cluster:

  ```
  kubectl apply -f https://raw.githubusercontent.com/kata-containers/packaging/master/kata-deploy/kata-rbac.yaml
  kubectl apply -f https://raw.githubusercontent.com/kata-containers/packaging/master/kata-deploy/kata-deploy.yaml
  ```

- Deploy a pod to use `kata-fc` runtime:

  ```
  kubectl apply -f https://raw.githubusercontent.com/kata-containers/packaging/master/kata-deploy/examples/test-deploy-kata-fc.yaml
  ```

- Get the pod listing:

	```
	kubectl get pods
	NAME                                  READY   STATUS    RESTARTS   AGE
	php-apache-kata-fc-747ccd546c-cdrdg   1/1     Running   0          43s
	```

- Verify that Firecracker is used as runtime:

	```
	HOW?
	```

