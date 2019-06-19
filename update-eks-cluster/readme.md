# Update EKS cluster

These instructions explain how to update an EKS cluster created using [eksctl](https://eksctl.io). [Updating an Amazon EKS Cluster Kubernetes Version](https://docs.aws.amazon.com/eks/latest/userguide/update-cluster.html) provide detailed instructions.

Read Kubernetes [version skew policy](https://kubernetes.io/docs/setup/release/version-skew-policy/).

## Create EKS 1.11 cluster

```
eksctl create cluster --name upgrade-test --version 1.11
```

Check k8s version:

```
kubectl version
Client Version: version.Info{Major:"1", Minor:"14", GitVersion:"v1.14.3", GitCommit:"5e53fd6bc17c0dec8434817e69b04a25d8ae0ff0", GitTreeState:"clean", BuildDate:"2019-06-07T09:57:54Z", GoVersion:"go1.12.5", Compiler:"gc", Platform:"darwin/amd64"}
Server Version: version.Info{Major:"1", Minor:"11+", GitVersion:"v1.11.8-eks-7c34c0", GitCommit:"7c34c0d2f2d0f11f397d55a46945193a0e22d8f3", GitTreeState:"clean", BuildDate:"2019-03-01T22:49:39Z", GoVersion:"go1.10.8", Compiler:"gc", Platform:"linux/amd64"}
```

## Do things

- Install Helm:

	```
	kubectl -n kube-system create sa tiller
	kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
	helm init --service-account tiller
	```

- Deploy a Helm chart:

	```
	git clone https://github.com/aws-samples/kubernetes-for-java-developers
	cd kubernetes-for-java-developers
	helm install --name myapp manifests/myapp
	```

- Verify application:

	```
	curl http://$(kubectl get svc/myapp-greeting \
		-o jsonpath='{.status.loadBalancer.ingress[0].hostname}')/hello
	```

## Update cluster

### Update control plane

- Update control plane:

	```
	eksctl update cluster --name upgrade-test --approve
	[ℹ]  using region us-west-2
	[ℹ]  re-building cluster stack "eksctl-upgrade-test-cluster"
	[✔]  all resources in cluster stack "eksctl-upgrade-test-cluster" are up-to-date
	[ℹ]  checking security group configuration for all nodegroups
	[ℹ]  all nodegroups have up-to-date configuration
	[ℹ]  will upgrade cluster "upgrade-test" control plane from current version "1.11" to "1.12"
	[✔]  cluster "upgrade-test" control plan e has been upgraded to version "1.12"
	[ℹ]  you will need to follow the upgrade procedure for all of nodegroups and add-ons
	```

	Check k8s version:

	```
	kubectl version
	Client Version: version.Info{Major:"1", Minor:"14", GitVersion:"v1.14.3", GitCommit:"5e53fd6bc17c0dec8434817e69b04a25d8ae0ff0", GitTreeState:"clean", BuildDate:"2019-06-07T09:57:54Z", GoVersion:"go1.12.5", Compiler:"gc", Platform:"darwin/amd64"}
	Server Version: version.Info{Major:"1", Minor:"12+", GitVersion:"v1.12.6-eks-d69f1b", GitCommit:"d69f1bf3669bf00b7f4a758e978e0e7a1e3a68f7", GitTreeState:"clean", BuildDate:"2019-02-28T20:26:10Z", GoVersion:"go1.10.8", Compiler:"gc", Platform:"linux/amd64"}
	```

- Update kube-proxy:

	```
	eksctl utils update-kube-proxy --name upgrade-test --approve
	[ℹ]  using region us-west-2
	[ℹ]  "kube-proxy" is now up-to-date
	```

- Get `coredns` version:

	```
	kubectl describe deployment coredns --namespace kube-system | grep Image | cut -d "/" -f 3
	coredns:v1.1.3
	```

- Update `coredns` version:

	```
	eksctl utils update-coredns --name upgrade-test --approve
	[ℹ]  using region us-west-2
	[ℹ]  "coredns" is now up-to-date
	```

### Update data plane

- Get nodegroups

	```
	eksctl get nodegroups --cluster upgrade-test 
	CLUSTER		NODEGROUP	CREATED			MIN SIZE	MAX SIZE	DESIRED CAPACITY	INSTANCE TYPE	IMAGE ID
	upgrade-test	ng-5351a550	2019-06-19T06:29:40Z	2		2		0			m5.large	ami-05ecac759c81e0b0c
	```

- Launch a new worker node group:

	```
	eksctl create nodegroup \
		--cluster upgrade-test \
		--version 1.12 \
		--name ng-1-12 \
		--node-type m5.large \
		--nodes 2 \
		--nodes-min 2 \
		--nodes-max 2 \
		--node-ami auto
	```

- Get nodes:

	```
	kubectl get nodes
	NAME                                           STATUS   ROLES    AGE     VERSION
	ip-192-168-30-170.us-west-2.compute.internal   Ready    <none>   36s     v1.12.7
	ip-192-168-62-120.us-west-2.compute.internal   Ready    <none>   5h13m   v1.11.9
	ip-192-168-64-15.us-west-2.compute.internal    Ready    <none>   41s     v1.12.7
	ip-192-168-88-245.us-west-2.compute.internal   Ready    <none>   5h13m   v1.11.9
	```

- Delete old nodegroup:

	```
	eksctl delete nodegroup --cluster upgrade-test --name ng-5351a550
	[ℹ]  include rules: ng-5351a550
	[ℹ]  1 nodegroup (ng-5351a550) was included
	[ℹ]  will delete 1 nodegroups from auth ConfigMap in cluster "upgrade-test"
	[ℹ]  removing role "arn:aws:iam::091144949931:role/eksctl-upgrade-test-nodegroup-ng-NodeInstanceRole-Z09UBQA0S9QZ" from auth ConfigMap (username = "system:node:{{EC2PrivateDNSName}}", groups = ["system:bootstrappers" "system:nodes"])
	[ℹ]  will drain 1 nodegroups in cluster "upgrade-test"
	[ℹ]  cordon node "ip-192-168-62-120.us-west-2.compute.internal"
	[ℹ]  cordon node "ip-192-168-88-245.us-west-2.compute.internal"
	[!]  ignoring DaemonSet-managed Pods: kube-system/aws-node-cl846, kube-system/kube-proxy-x6s95
	[!]  ignoring DaemonSet-managed Pods: kube-system/aws-node-zgs82, kube-system/kube-proxy-fppld
	[!]  ignoring DaemonSet-managed Pods: kube-system/aws-node-cl846, kube-system/kube-proxy-x6s95
	[!]  ignoring DaemonSet-managed Pods: kube-system/aws-node-zgs82, kube-system/kube-proxy-fppld
	[✔]  drained nodes: [ip-192-168-62-120.us-west-2.compute.internal ip-192-168-88-245.us-west-2.compute.internal]
	[ℹ]  will delete 1 nodegroups from cluster "upgrade-test"
	[ℹ]  1 task: { delete nodegroup "ng-5351a550" [async] }
	[ℹ]  will delete stack "eksctl-upgrade-test-nodegroup-ng-5351a550"
	[✔]  deleted 1 nodegroups from cluster "upgrade-test"
	```

- Get nodes:

	```
	kubectl get nodes
	NAME                                           STATUS                     ROLES    AGE     VERSION
	ip-192-168-30-170.us-west-2.compute.internal   Ready                      <none>   2m26s   v1.12.7
	ip-192-168-62-120.us-west-2.compute.internal   Ready,SchedulingDisabled   <none>   5h15m   v1.11.9
	ip-192-168-64-15.us-west-2.compute.internal    Ready                      <none>   2m31s   v1.12.7
	ip-192-168-88-245.us-west-2.compute.internal   Ready,SchedulingDisabled   <none>   5h15m   v1.11.9
	```

	Takes a few mins for the older nodes to terminate and disappear.
