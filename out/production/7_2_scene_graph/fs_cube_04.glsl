#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform vec3 viewPos;
uniform sampler2D first_texture;
uniform sampler2D second_texture;

struct Light {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
};

uniform Light light1;
uniform Light light2;

struct Material {
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;
};

uniform Material material;

void main() {
  //Light 1
  // ambient

  vec3 totalDiffuse = vec3(0.0);
  vec3 totalSpecular = vec3(0.0);
  vec3 ambient = light1.ambient * vec3(texture(first_texture, aTexCoord));
  // diffuse
  vec3 norm = normalize(aNormal);
  vec3 lightDir = normalize(light1.position - aPos);
  float diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + light1.diffuse * diff * vec3(texture(first_texture, aTexCoord));

  // specular
  vec3 viewDir = normalize(viewPos - aPos);
  vec3 reflectDir = reflect(-lightDir, norm);
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + light1.specular * spec * vec3(texture(second_texture, aTexCoord));

  //light 2

  ambient = light2.ambient * vec3(texture(first_texture, aTexCoord));
  // diffuse
  norm = normalize(aNormal);
  lightDir = normalize(light2.position - aPos);
  diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + light2.diffuse * diff * vec3(texture(first_texture, aTexCoord));

  // specular
  viewDir = normalize(viewPos - aPos);
  reflectDir = reflect(-lightDir, norm);
  spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + light2.specular * spec * vec3(texture(second_texture, aTexCoord));

  vec3 result = ambient + totalDiffuse + totalSpecular;
  fragColor = vec4(result, 1.0);
}