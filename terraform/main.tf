terraform {
  required_version = ">= 1.14.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 6.35.0"
    }
  }
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "db_password" {
  description = "PostgreSQL password. Set via TF_VAR_db_password or terraform.tfvars"
  type        = string
  sensitive   = true
  default     = "CHANGE_ME"
}

variable "keystore_password" {
  description = "TLS keystore password. Set via TF_VAR_keystore_password or terraform.tfvars"
  type        = string
  sensitive   = true
  default     = "CHANGE_ME"
}

provider "aws" {

  region = var.aws_region
  default_tags {
    tags = {
      terraform     = "true"
      application   = "DC PASS"
    }
  }
}

data "aws_caller_identity" "current" {}

# -----------------------------------------------------------------------------
# IAM User for Lightsail instance (S3/SSM access)
# -----------------------------------------------------------------------------

resource "aws_iam_user" "dcpass" {
  name = "dcpass-lightsail"
  path = "/"
}

resource "aws_iam_access_key" "dcpass" {
  user = aws_iam_user.dcpass.name
}

resource "aws_iam_user_policy" "dcpass" {
  name   = "dcpass-lightsail-policy"
  user   = aws_iam_user.dcpass.name
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "SSMParameters"
        Effect = "Allow"
        Action = [
          "ssm:GetParameter",
          "ssm:GetParameters"
        ]
        Resource = "arn:aws:ssm:${var.aws_region}:${data.aws_caller_identity.current.account_id}:parameter/dcpass/*"
      }
    ]
  })
}

# -----------------------------------------------------------------------------
# SSM Parameters (SecureString - encrypted at rest)
# -----------------------------------------------------------------------------

resource "aws_ssm_parameter" "db_password" {
  name        = "/dcpass/db-password"
  description = "PostgreSQL password for dcpass application"
  type        = "SecureString"
  value       = var.db_password

  lifecycle {
    ignore_changes = [value]
  }
}

resource "aws_ssm_parameter" "keystore_password" {
  name        = "/dcpass/keystore-password"
  description = "TLS keystore password for dcpass application"
  type        = "SecureString"
  value       = var.keystore_password

  lifecycle {
    ignore_changes = [value]
  }
}

# -----------------------------------------------------------------------------
# Outputs
# -----------------------------------------------------------------------------

output "iam_access_key_id" {
  value     = aws_iam_access_key.dcpass.id
  sensitive = true
}

output "iam_secret_access_key" {
  value     = aws_iam_access_key.dcpass.secret
  sensitive = true
}