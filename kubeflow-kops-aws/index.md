# Install Kubeflow on self-managed Kubernetes on AWS

This post will explain how to setup Kubeflow an self-managed Kubernetes cluster on AWS. Even though it uses kops for creating the cluster but it could've been created any other way, such as CloudFormation or Terraform.

- Install kops:

	```
  brew update && brew upgrade kops
  ```

- Setup env vars:

	```
	export NAME=kops.k8s.local
	export KOPS_STATE_STORE=s3://kops-state-store-aws
	export AWS_AVAILABILITY_ZONES="$(aws ec2 describe-availability-zones --query 'AvailabilityZones[].ZoneName' --output text | awk -v OFS="," '$1=$1')"
	```

- Create cluster:

	```
	kops create cluster ${NAME} --node-count=4 --zones=${AWS_AVAILABILITY_ZONES} --node-size=m5.2xlarge --master-size=m5.xlarge
	kops update cluster --name ${NAME} --yes
	```

- Optionally, install Kubernetes dashboard:

	```
	kubectl create -f https://raw.githubusercontent.com/kubernetes/kops/master/addons/kubernetes-dashboard/v1.10.1.yaml

	```

- Download Kubeflow:

	```
	curl -OL https://github.com/kubeflow/kubeflow/releases/download/v0.6.1/kfctl_v0.6.1_darwin.tar.gz
	```

- Setup Kubeflow:

	```
	export PATH=$PATH:/Users/argu/tools/kubeflow/0.6.1
	export KFAPP=kfapp
	export CONFIG="https://raw.githubusercontent.com/kubeflow/kubeflow/master/bootstrap/config/kfctl_k8s_istio.yaml"

	# Specify credentials for the default user.
	export KUBEFLOW_USER_EMAIL="admin@kubeflow.org"
	export KUBEFLOW_PASSWORD="12341234"

	kfctl init ${KFAPP} --config=${CONFIG} -V
	cd ${KFAPP}
	kfctl generate all -V
	kfctl apply all -V
	```

## Dashboard using Istio Ingress Gateway

### Using NodePort

- Get internal IP address of the EC2 instance where `istio-ingressgateway` pod is running:

	```
	kubectl get pods -n istio-system -l app=istio-ingressgateway,istio=ingressgateway,release=istio --output=wide
	NAME                                    READY   STATUS    RESTARTS   AGE    IP           NODE                                           NOMINATED NODE
	istio-ingressgateway-5f55c95767-5ldtg   1/1     Running   0          135m   100.96.3.4   ip-172-20-123-226.us-west-2.compute.internal   <none>
	```

- Get public IP address:

	```
	aws ec2 describe-instances \
	--filters Name=private-dns-name,Values=ip-172-20-123-226.us-west-2.compute.internal \
	--query "Reservations[0].Instances[0].PublicDnsName" \
	--output text
	```

- Enable port 80 access in the security group
- Access istio ingress endpoint:

	```
	open https://<public-ip>:80
	```

	The service endpoint is inaccessible.

## Using LoadBalancer

- Run proxy:

	```
	kubectl proxy
	```

-	Access Kubernetes Dashboard http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/
- Select `Token`
- Generate token:

	```
	kops get secrets --type secret admin -oplaintext
	```

- Click on `SIGN IN`
- Access `istio-ingressgateway` in the dashboard [http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/service/istio-system/istio-ingressgateway?namespace=istio-system]
- Click on `EDIT` (top right)
- Replace `NodePort` with `LoadBalancer`
- Click on `Update`
- Wait for 3 minutes for the load balancer to be deployed
- Get endpoint address:

	```
	kubectl get svc -n istio-system istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
	```

- Access in the browser, now everything is working.
