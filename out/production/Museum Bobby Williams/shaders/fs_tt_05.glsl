#version 330 core
/* I declare that this code is my own work */
/* Author Bobby Williams bobby.james.williams@outlook.com */
/* Extending The work of Steve Maddock s.maddock@sheffield.ac.uk, with some additions by Bobby Williams */



in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;
uniform vec3 viewPos;

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

  vec3 totalDiffuse = vec3(0.0);
  vec3 totalSpecular = vec3(0.0);
  // ambient
  vec3 ambient = ((light1.ambient + light2.ambient)/2) * material.ambient * texture(first_texture, aTexCoord).rgb;

  //SpotLight
  //diffuse
  vec3 norm = normalize(aNormal);
  vec3 lightDir = normalize(spotLight.position - aPos);
  float diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = (totalDiffuse + spotLight.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb) * spotLight.intensity_mod;

  // specular
  vec3 viewDir = normalize(viewPos - aPos);
  vec3 reflectDir = reflect(-lightDir, norm);
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = (totalSpecular + spotLight.specular * (spec * material.specular)) * spotLight.intensity_mod;

  // spotlight
  float a = dot(lightDir, normalize(-spotLight.direction));
  float b = (spotLight.cutOff - spotLight.outerCutOff);
  float intensity = clamp((a - spotLight.outerCutOff) / b, 0.0, 1.0);
  totalDiffuse *= intensity;
  totalSpecular *= intensity;


  //######################################################################################################################
  //light 1
  // diffuse
  norm = normalize(aNormal);
  lightDir = normalize(light1.position - aPos);
  diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + light1.diffuse * ((diff * material.diffuse) * texture(first_texture, aTexCoord).rgb) * light1.intensity_mod;

  // specular
  viewDir = normalize(viewPos - aPos);
  reflectDir = reflect(-lightDir, norm);
  spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + light1.specular * ((spec * material.specular))* light1.intensity_mod;


//#####################################################################################################################
  //light 2
 //diffuse
  norm = normalize(aNormal);
  lightDir = normalize(light2.position - aPos);
  diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + (light2.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb) * light2.intensity_mod;

  // specular
  viewDir = normalize(viewPos - aPos);
  reflectDir = reflect(-lightDir, norm);
  spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + (light2.specular * (spec * material.specular)) * light2.intensity_mod;
//#####################################################################################################################

  vec3 result = (ambient * (light1.intensity_mod + light2.intensity_mod)/2) + totalDiffuse + totalSpecular;
  fragColor = vec4(result, 1.0);
}