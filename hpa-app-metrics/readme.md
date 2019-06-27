# Horizontal Pod Autoscaler with application metrics

- Create an EKS cluster:

	```
	eksctl create cluster \
		--name myeks \
		--node-type m5.2xlarge \
		--nodes 2 \
		--nodes-min 1 \
		--nodes-max 2
	```

- Install Helm:

	```
	kubectl -n kube-system create sa tiller
	kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
	helm init --service-account tiller
	```

- Deploy the metrics server:

	```
	helm install stable/metrics-server \
		--name metrics-server \
		--version 2.8.2 \
		--namespace metrics
	```

  Confirm the Metrics API is available:

  ```
  kubectl get apiservice v1beta1.metrics.k8s.io -o yaml
  NAME                     SERVICE                  AVAILABLE   AGE
	v1beta1.metrics.k8s.io   metrics/metrics-server   True        2m
  ```

- Check node resource usage:

	```
	kubectl top node
	NAME                                          CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%   
	ip-192-168-2-219.us-west-2.compute.internal   39m          0%     383Mi           1%        
	ip-192-168-33-52.us-west-2.compute.internal   34m          0%     327Mi           1%    
	```