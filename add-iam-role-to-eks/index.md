# Add IAM role to Amazon EKS Cluster

This script shows how to attach an IAM role from a _destination_ machine to access EKS cluster created on a _master_ machine.

## Destination Machine

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

  Note value of `Role.Arn` property.
- Install aws-iam-authenticator:

	```
	brew install aws-iam-authenticator
	```

## Master Machine

- Replace `$Arn` from the destination machine in the script below. Add IAM role to `aws-auth` ConfigMap for the EKS cluster:

	```
	ROLE="    - rolearn: $Arn\n      username: eks\n      groups:\n        - system:masters"
	kubectl get -n kube-system configmap/aws-auth -o yaml | awk "/mapRoles: \|/{print;print \"$ROLE\";next}1" > /tmp/aws-auth-patch.yml
	kubectl patch configmap/aws-auth -n kube-system --patch "$(cat /tmp/aws-auth-patch.yml)"
	```

- Generate configuration file to access the EKS Cluster:

	```
	eksctl utils write-kubeconfig --name myeks --kubeconfig ./kubeconfig
	```

- Copy `kubeconfig` to the destination machine

## Destination Machine

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

