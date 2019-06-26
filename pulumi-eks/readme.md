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

- Install AWS IAM Authenticator: https://docs.aws.amazon.com/eks/latest/userguide/install-aws-iam-authenticator.html and include in the path

- Create EKS cluster:

	```
	cd myeks
	pulumi up --yes
	```