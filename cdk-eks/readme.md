# Create Amazon EKS cluster with AWS CDK

- Install AWS CDK:

	```
	npm install -g aws-cdk
	```

	Chek the version:

	```
	cdk --version
	0.36.0 (build 6d38487)
	```

- Use the [CDK application](https://github.com/arun-gupta/arun-gupta.github.io/tree/master/cdk-eks/myeks), compile the stack:

	```
	mvn compile
	```

	NOTE: This will only create EKS control plane. Data Plane is tracked at https://github.com/awslabs/aws-cdk/issues/3100.

- Deploy the stack:

	```
	cdk deploy
	```