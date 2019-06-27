# Create Amazon EKS cluster with Pulumi

- Install Pulumi:

	```
	brew install pulumi
	```

	Verify version:

	```
	pulumi version
	v0.17.19
	```

- Install [AWS IAM Authenticator](https://docs.aws.amazon.com/eks/latest/userguide/install-aws-iam-authenticator.html) and include in the path.

- The code for EKS cluster is in [myeks](https://github.com/arun-gupta/arun-gupta.github.io/blob/master/pulumi-eks/myeks) diretory. Create EKS cluster:

	```
	cd myeks
	pulumi up --yes
	```

- Access the cluster:

	```
	pulumi stack output kubeconfig > kubeconfig
	kubectl --kubeconfig kubeconfig version --short
	Client Version: v1.15.0
	Server Version: v1.12.6-eks-d69f1b
	kubectl --kubeconfig kubeconfig get nodes
	NAME                          STATUS   ROLES    AGE   VERSION
	ip-10-0-157-75.ec2.internal   Ready    <none>   17m   v1.12.7
	ip-10-0-62-175.ec2.internal   Ready    <none>   17m   v1.12.7
	```

- Destroy the cluster:

  ```
  pulumi destroy --yes
  ```

