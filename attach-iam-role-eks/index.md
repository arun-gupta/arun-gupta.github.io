= Add IAM users to Amazon EKS Cluster

== Master Machine

- Write kube config:

	eksctl utils write-kubeconfig --name myeks --kubeconfig ./kubeconfig

- 

== Designated Machine

- Create an IAM role:

	aws iam create-role \
		--role-name myeksrole \
		--assume-role-policy-document file://trust-policy.json

- Install aws-iam-authenticator:

	brew install aws-iam-authenticator

