#version 330 core

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
};

struct SpotLight {
  vec3 position;
  vec3 direction;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;

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
  totalDiffuse = totalDiffuse + spotLight.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb;

  // specular
  vec3 viewDir = normalize(viewPos - aPos);
  vec3 reflectDir = reflect(-lightDir, norm);
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + spotLight.specular * (spec * material.specular);

  // spotlight
  float theta = dot(lightDir, normalize(-spotLight.direction));
  float epsilon = (spotLight.cutOff - spotLight.outerCutOff);
  float intensity = clamp((theta - spotLight.outerCutOff) / epsilon, 0.0, 1.0);
  totalDiffuse *= intensity;
  totalSpecular *= intensity;


  //######################################################################################################################
  //light 1
  // diffuse
  norm = normalize(aNormal);
  lightDir = normalize(light1.position - aPos);
  diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + light1.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb;

  // specular
  viewDir = normalize(viewPos - aPos);
  reflectDir = reflect(-lightDir, norm);
  spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + light1.specular * (spec * material.specular);


//#####################################################################################################################
  //light 2
 //diffuse
  norm = normalize(aNormal);
  lightDir = normalize(light2.position - aPos);
  diff = max(dot(norm, lightDir), 0.0);
  totalDiffuse = totalDiffuse + light2.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb;

  // specular
  viewDir = normalize(viewPos - aPos);
  reflectDir = reflect(-lightDir, norm);
  spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  totalSpecular = totalSpecular + light2.specular * (spec * material.specular);
//#####################################################################################################################


  vec3 result = ambient + totalDiffuse + totalSpecular;
  fragColor = vec4(result, 1.0);
}