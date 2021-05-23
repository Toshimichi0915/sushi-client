#version 110
uniform float time;
varying float factor;

vec3 rainbow(float x)
{
    float level = floor(x * 6.0);
    float r = float(level <= 2.0) + float(level > 4.0) * 0.5;
    float g = max(1.0 - abs(level - 2.0) * 0.5, 0.0);
    float b = (1.0 - (level - 4.0) * 0.5) * float(level >= 4.0);
    return vec3(r, g, b);
}

void main()
{
    gl_FragColor = vec4(rainbow(factor), 1.0);
}