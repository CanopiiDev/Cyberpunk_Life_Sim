import { formatMoney } from "../../utils/format.js";

// Lijst met jobs. Klik op "Doe job" om geld en Street Cred te verdienen.
export default function JobList({ jobs, onDoJob }) {
  return (
    <div className="card">
      {jobs.map((job) => (
        <div key={job.id} className="list-item">
          <div>
            <strong>{job.name}</strong>
            <p className="muted">{job.description}</p>
            <p className="small-text">
              +{formatMoney(job.payout)} · +{job.streetCredReward} cred · -{job.healthCost} health
              {job.requiredStreetCred > 0 && ` · vereist ${job.requiredStreetCred} cred`}
              {job.requiredSmarts > 0 && ` · vereist ${job.requiredSmarts} smarts`}
            </p>
          </div>
          <button className="small" onClick={() => onDoJob(job.id)}>
            Doe job
          </button>
        </div>
      ))}
    </div>
  );
}
