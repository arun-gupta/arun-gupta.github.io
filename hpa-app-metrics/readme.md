# Horizontal Pod Autoscaler with application metrics

## Setup metrics server

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
  kubectl get --raw /apis/metrics.k8s.io/v1beta1  | jq
	{
	  "kind": "APIResourceList",
	  "apiVersion": "v1",
	  "groupVersion": "metrics.k8s.io/v1beta1",
	  "resources": [
	    {
	      "name": "nodes",
	      "singularName": "",
	      "namespaced": false,
	      "kind": "NodeMetrics",
	      "verbs": [
	        "get",
	        "list"
	      ]
	    },
	    {
	      "name": "pods",
	      "singularName": "",
	      "namespaced": true,
	      "kind": "PodMetrics",
	      "verbs": [
	        "get",
	        "list"
	      ]
	    }
	  ]
	}
  ```

- Check node resource usage:

	```
	kubectl top node
	NAME                                          CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%   
	ip-192-168-2-219.us-west-2.compute.internal   39m          0%     383Mi           1%        
	ip-192-168-33-52.us-west-2.compute.internal   34m          0%     327Mi           1%    
	```

## Deploy application

- Get application:

	```
	git clone https://github.com/arun-gupta/spring-boot-prometheus
	```

- Deploy application:

	```
	kubectl create -f k8s.yaml
	```

How to aggregate app metrics with Prometheus running in EKS?

## Setup HPA

- Custom metrics are not enabled by default:

	```
	kubectl get --raw /apis/custom.metrics.k8s.io/v1beta1  | jq
	Error from server (NotFound): the server could not find the requested resource
	```

- Deploy Prometheus operator, this will also enable custom metrics:

	```
	helm install --name my-release stable/prometheus-adapter
	```

- Check custom metrics again:

	```
	kubectl get --raw /apis/custom.metrics.k8s.io/v1beta1  | jq
	{"kind":"APIResourceList","apiVersion":"v1","groupVersion":"custom.metrics.k8s.io/v1beta1","resources":[]}
	```

- 