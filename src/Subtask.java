public class Subtask extends Epic {
    private int epicIdForSubtask;

    public Subtask(String taskName, String taskDescription, String status) {
        super(taskName, taskDescription, status);
    }

    public int getEpicIdForSubtask() {
        return epicIdForSubtask;
    }

    public void setEpicIdForSubtask(int epicId) {
        this.epicIdForSubtask = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                '}' + "\n";
    }
}
