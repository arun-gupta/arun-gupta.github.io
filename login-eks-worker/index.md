# Login to EKS Worker Nodes

1. Get list of the nodes:

   ```
   kubectl get nodes
   NAME                                           STATUS   ROLES    AGE   VERSION
   ip-192-168-40-127.us-west-2.compute.internal   Ready    <none>   10m   v1.11.9
   ip-192-168-72-76.us-west-2.compute.internal    Ready    <none>   10m   v1.11.9
   ```

1. Get IP address of one of the worker nodes:

   ```
   aws ec2 describe-instances \
   --filters Name=private-dns-name,Values=$(kubectl get nodes -o jsonpath='{.items[0].metadata.name}') \
   --query "Reservations[0].Instances[0].PublicDnsName" \
   --output text
   ```

   Change the index in `items[0]` to log in to a different worker.

1. Login to worker nodes:

   ```
   ssh -i ~/.ssh/arun-us-west2.pem ec2-user@<worker-ip>
   ```
