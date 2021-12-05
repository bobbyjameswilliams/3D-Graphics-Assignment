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
  float intensity_mod;
};
struct SpotLight {
  vec3 position;
  vec3 direction;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float intensity_mod;

  float cutOff;
  float outerCutOff;

};

uniform Light light1;
uniform Light light2;
uniform SpotLight spotLight;

struct Material {
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;
};

uniform Material material;

void main() {

  //SpotLight
  vec3 totalDiffuse = vec3(0.0);
  vec3 totalSpecular = vec3(0.0);
  vec3 ambient = ((light1.ambient + light2.ambient)/2) * material.ambient * texture(first_texture, aTexCoord).rgb;

  // diffuse
  vec3 norm = normalize(aNormal);
  vec3 lightDir = normalize(spotLight.position - aPos);
  float diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = (totalDiffuse + spotLight.diffuse * diff * vec3(texture(first_texture, aTexCoord))) * spotLight.intensity_mod;
  // specular
  vec3 viewDir = normalize(viewPos - aPos);
  vec3 reflectDir = reflect(-lightDir, norm);
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = (totalSpecular + spotLight.specular * spec * vec3(texture(second_texture, aTexCoord))) * spotLight.intensity_mod;
  // spotlight
  float theta = dot(lightDir, normalize(-spotLight.direction));
  float epsilon = (spotLight.cutOff - spotLight.outerCutOff);
  float intensity = clamp((theta - spotLight.outerCutOff) / epsilon, 0.0, 1.0);
  totalDiffuse *= intensity;
  totalSpecular *= intensity;

  //utils.Light 1
  // diffuse
  norm = normalize(aNormal);
  lightDir = normalize(light1.position - aPos);
  diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + (light1.diffuse * diff * vec3(texture(first_texture, aTexCoord))) * light1.intensity_mod;

  // specular
  viewDir = normalize(viewPos - aPos);
  reflectDir = reflect(-lightDir, norm);
  spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + (light1.specular * spec * vec3(texture(second_texture, aTexCoord))) * light1.intensity_mod;

  //light 2

  ambient = ((light1.ambient + light2.ambient)/2) * vec3(texture(first_texture, aTexCoord));
  // diffuse
  norm = normalize(aNormal);
  lightDir = normalize(light2.position - aPos);
  diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + (light2.diffuse * diff * vec3(texture(first_texture, aTexCoord))) * light2.intensity_mod;

  // specular
  viewDir = normalize(viewPos - aPos);
  reflectDir = reflect(-lightDir, norm);
  spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + (light2.specular * spec * vec3(texture(second_texture, aTexCoord))) * light2.intensity_mod;

  vec3 result = (ambient * (light1.intensity_mod + light2.intensity_mod)/2) + totalDiffuse + totalSpecular;
  fragColor = vec4(result, 1.0);
}