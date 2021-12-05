import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import utils.Model;

public class Mobile {
    private GL3 gl;
    private Model mobilePhone;
    private Model phoneBaseCube;
    private SGNode phoneRoot;

    private TransformNode phoneMoveTranslate;

    private float phoneScale = 5f;

    public Mobile(GL3 gl, Model mobilePhone, Model phoneBaseCube){
        this.gl = gl;
        this.mobilePhone = mobilePhone;
        this.phoneBaseCube = phoneBaseCube;
        this.sceneGraph();
    }

    private void sceneGraph(){
        phoneRoot = new NameNode("root");
        phoneMoveTranslate = new TransformNode("phone transform", Mat4Transform.translate(15, 0, -15f));
        TransformNode phoneTranslate = new TransformNode("phone transform",Mat4Transform.translate(0,0,0));

        NameNode phoneBase = new NameNode("phone base");
        Mat4 m = Mat4Transform.translate(0,phoneScale/4,0);
        m = Mat4.multiply(m, Mat4Transform.scale((phoneScale),phoneScale/2,(phoneScale)));
        TransformNode phoneBaseTransform =  new TransformNode("phone base transform", m);
        ModelNode phoneBaseShape = new ModelNode("vertexes.Cube(phone base)", phoneBaseCube);


        NameNode phone = new NameNode("phone");
        m = Mat4Transform.translate(0,phoneScale + phoneScale/2,0);
        m = Mat4.multiply(m, Mat4Transform.scale((phoneScale),(phoneScale * 2),(phoneScale / 4)));
        TransformNode phoneTransform = new TransformNode("phone transform", m);
        ModelNode phoneShape = new ModelNode("vertexes.Cube(phone)", mobilePhone);

        phoneRoot.addChild(phoneMoveTranslate);
            phoneMoveTranslate.addChild(phoneTranslate);
                phoneTranslate.addChild(phoneBase);
                    phoneBase.addChild(phoneBaseTransform);
                        phoneBaseTransform.addChild(phoneBaseShape);
                    phoneBase.addChild(phone);
                        phone.addChild(phoneTransform);
                            phoneTransform.addChild(phoneShape);

        phoneRoot.update();
    }

    public void render(){
        phoneRoot.draw(gl);
    }
}
