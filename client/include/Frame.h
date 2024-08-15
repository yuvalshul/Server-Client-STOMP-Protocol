#include <string>
using std::string;

class Frame
{
    private:
    string stompCommand;
    string headers;
    string body;

    public:
    Frame(string stompCommand);
    Frame();
    void setStompCommand(string stompCommand);
    void setHeaders(string headers);
    void setBody(string body);
    static Frame sendFrame(string destination, string message, string optionalRecieptId);
    static Frame connectFrame(string login, string passcode, string optionalRecieptId);
    static Frame subscribeFrame(string destination, int subId, string optionalRecieptId);
    static Frame unsubscribeFrame(int subId, string optionalRecieptId);
    static Frame disconnectFrame(string recieptId);
    string toString();


};