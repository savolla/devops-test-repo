output "cluster_id" {
  value = aws_eks_cluster.savolla.id
}

output "node_group_id" {
  value = aws_eks_node_group.savolla.id
}

output "vpc_id" {
  value = aws_vpc.savolla_vpc.id
}

output "subnet_ids" {
  value = aws_subnet.savolla_subnet[*].id
}
