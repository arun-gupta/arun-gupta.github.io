# Add IAM role to Amazon EKS Cluster

These instructions explain how to access Amazon EKS cluster from a _destination_ machine by somebody who does not have an AWS account. The cluster is created on a _source_ machine.

## Create AWS Destination User (using _source_ credentials)

- Create a new policy at https://console.aws.amazon.com/iam/home?region=us-west-2#/policies. Call it `AmazonEKSAdminPolicy` and use the following JSON fragment:

	```
	{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Action": [
          "eks:*"
        ],
        "Resource": "*"
      }
    ]
	}
	```
- Create a new group at https://console.aws.amazon.com/iam/home?region=us-west-2#/groups, call it `myeks`. Assign previously created policy to the group.
- Create a [new AWS user](https://console.aws.amazon.com/iam/home?region=us-west-2#/users), enable programmatic access, add user to the `myeks` group
- Download `.csv` file and share the credentials out of band

## Create IAM role (using _source_ credentials)

- Create an IAM role using [trust-policy.json](trust-policy.json):

	```
	aws iam create-role \
		--role-name myeksrole \
		--assume-role-policy-document file://trust-policy.json
	```

  Shows the output:

  ```
	{
    "Role": {
      "Path": "/",
      "RoleName": "myeksrole",
      "RoleId": "AROARKOFJSCVYESJO2ZZT",
      "Arn": "arn:aws:iam::<account-id>:role/myeksrole",
      "CreateDate": "2019-05-13T15:00:16Z",
      "AssumeRolePolicyDocument": {
        "Version": "2012-10-17",
        "Statement": [
          {
            "Effect": "Allow",
            "Principal": {
              "Service": "eks.amazonaws.com"
            },
            "Action": "sts:AssumeRole"
          }
        ]
      }
    }
	}
	```

  Note the value of `Role.Arn` property.

## Configure AWS CLI (using _destination_ credentials)

- Install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
- [Configure](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html). For convenience, `aws configure` command will configure the CLI using the given credentials. Make sure to choose the same region in which the EKS cluster is created, for example `us-west-2`.
- Install aws-iam-authenticator:

	```
	brew install aws-iam-authenticator
	```

## Add Destination IAM role to EKS Cluster

- Replace `$Arn` from the destination user in the script below. Add IAM role to `aws-auth` ConfigMap for the EKS cluster:

	```
	ROLE="    - rolearn: $Arn\n      username: eks\n      groups:\n        - system:masters"
	kubectl get -n kube-system configmap/aws-auth -o yaml | awk "/mapRoles: \|/{print;print \"$ROLE\";next}1" > /tmp/aws-auth-patch.yml
	kubectl patch configmap/aws-auth -n kube-system --patch "$(cat /tmp/aws-auth-patch.yml)"
	```

## Generate Kubernetes Configuration for Destination User

- Generate configuration file to access the EKS Cluster:

	```
	aws eks update-kubeconfig \
		--role-arn $Role.Arn \
		--kubeconfig ./kubeconfig \
		--name <eks-cluster-name>
	```

- Copy `kubeconfig` where destination user can access it.

## Access EKS Cluster by Destination User

- Use the `kubeconfig` to access the cluster:

	```
	kubectl --kubeconfig ./kubeconfig get nodes
	```

	Optionally, set `KUBECONFIG` environment variable:

	```
	export KUBECONFIG=`pwd`/kubeconfig
	```

	And then get nodes as:

	```
	kubectl get nodes
	```

