terraform {
  backend "s3" {
    bucket         = "asap-cv-terraform-state"
    key            = "dcpass/terraform.tfstate"
    region         = "us-east-1"
  }
}