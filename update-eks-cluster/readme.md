# Update EKS cluster

## Create EKS cluster

```
eksctl create cluster --name upgrade-test --version 1.11
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

- Update control plane:

	```
	eksctl update cluster --name upgrade-test --approve
	```

- 
eksctl utils update-kube-proxy --name upgrade-test dev --approve
eksctl utils update-coredns --name upgrade-test --approve
```