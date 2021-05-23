#version 110
uniform float time;
varying float factor;

void main()
{
    factor = -gl_Vertex.y * 0.5 + 0.5;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
